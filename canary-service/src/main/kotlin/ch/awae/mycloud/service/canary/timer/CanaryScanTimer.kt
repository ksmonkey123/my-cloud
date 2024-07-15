package ch.awae.mycloud.service.canary.timer

import ch.awae.mycloud.auth.*
import ch.awae.mycloud.service.canary.model.*
import ch.awae.mycloud.service.canary.service.*
import net.javacrumbs.shedlock.spring.annotation.*
import org.springframework.scheduling.annotation.*
import org.springframework.stereotype.*

@Component
class CanaryScanTimer(
    private val monitoredSiteRepository: MonitoredSiteRepository,
    private val scanningService: ScanningService,
) {

    @SchedulerLock(name = "scan-timer", lockAtLeastFor = "PT20S")
    @Scheduled(cron = "\${canary.timer.schedule}")
    fun performScan() {
        AuthInfo.impersonate("scan-timer") {
            for (id in monitoredSiteRepository.findEnabledIds()) {
                scanningService.scan(id)
            }
        }
    }

}