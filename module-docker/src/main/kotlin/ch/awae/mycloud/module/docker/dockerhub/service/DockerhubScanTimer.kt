package ch.awae.mycloud.module.docker.dockerhub.service

import ch.awae.mycloud.common.*
import ch.awae.mycloud.module.docker.dockerhub.model.*
import net.javacrumbs.shedlock.spring.annotation.*
import org.springframework.scheduling.annotation.*
import org.springframework.stereotype.*

@Component
class DockerhubScanTimer(
    private val monitoredEntryRepository: MonitoredEntryRepository,
    private val dockerhubScanService: DockerhubScanService,
) {

    private val logger = createLogger()

    @SchedulerLock(name = "docker:dockerhub-scanner")
    @Scheduled(cron = "\${docker.timer.dockerhub}")
    fun performScan() {
        val ids = monitoredEntryRepository.listIdsOfEnabled()
        logger.info("found ${ids.size} entries to process")

        if (ids.isNotEmpty()) {
            for (id in ids) {
                try {
                    dockerhubScanService.runScan(id)
                } catch (e: Exception) {
                    logger.error("error during scan for entry $id", e)
                }
            }
        }
    }
}
