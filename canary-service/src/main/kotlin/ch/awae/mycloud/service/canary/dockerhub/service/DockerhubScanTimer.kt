package ch.awae.mycloud.service.canary.dockerhub.service

import ch.awae.mycloud.*
import ch.awae.mycloud.auth.*
import ch.awae.mycloud.service.canary.dockerhub.model.*
import net.javacrumbs.shedlock.spring.annotation.*
import org.springframework.scheduling.annotation.*
import org.springframework.stereotype.*

@Component
class DockerhubScanTimer(
    private val monitoredEntryRepository: MonitoredEntryRepository,
    private val dockerhubScanService: DockerhubScanService,
    private val dockerhubClientBuilder: DockerhubApiClient.Builder,
) {

    private val logger = createLogger()

    @SchedulerLock(name = "dockerhub-timer", lockAtLeastFor = "PT20S")
    @Scheduled(cron = "\${canary.timer.dockerhub}")
    fun performScan() {

        AuthInfo.impersonate("dockerhub-timer") {
            val ids = monitoredEntryRepository.listIdsOfEnabled()
            logger.info("found ${ids.size} entries to process")

            if (ids.isNotEmpty()) {
                val client = dockerhubClientBuilder.buildClient()

                for (id in ids) {
                    try {
                        dockerhubScanService.runScan(id, client)
                    } catch (e: Exception) {
                        logger.error("error during scan for entry $id", e)
                    }
                }
            }
        }
    }

}