package ch.awae.mycloud.module.canary.web.service

import ch.awae.mycloud.module.canary.web.model.*
import net.javacrumbs.shedlock.spring.annotation.*
import org.springframework.scheduling.annotation.*
import org.springframework.stereotype.*

@Component
class WebsiteScanTimer(
    private val monitoredSiteRepository: MonitoredSiteRepository,
    private val scanningService: ScanningService,
) {

    @SchedulerLock(name = "canary:web-scanner")
    @Scheduled(cron = "\${canary.timer.web}")
    fun performScan() {
        for (id in monitoredSiteRepository.findEnabledIds()) {
            scanningService.scan(id)
        }
    }

}