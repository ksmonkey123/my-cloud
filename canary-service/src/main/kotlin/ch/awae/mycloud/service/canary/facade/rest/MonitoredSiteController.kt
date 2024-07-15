package ch.awae.mycloud.service.canary.facade.rest

import ch.awae.mycloud.service.canary.dto.*
import ch.awae.mycloud.service.canary.service.*
import jakarta.validation.*
import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@RestController
@PreAuthorize("hasAuthority('canary')")
@RequestMapping("/monitored-site")
class MonitoredSiteController(
    private val monitoredSiteService: MonitoredSiteService,
) {

    @GetMapping
    fun listSites(): List<MonitoredSiteSummaryDto> {
        return monitoredSiteService.listAll()
    }

    @PostMapping
    fun createSite(@Valid @RequestBody request: MonitoredSiteDto): MonitoredSiteSummaryDto {
        return monitoredSiteService.create(request)
    }

    @PutMapping("/{id}")
    fun updateSite(@PathVariable id: Long, @Valid @RequestBody request: MonitoredSiteDto): MonitoredSiteSummaryDto {
        return monitoredSiteService.update(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSite(@PathVariable id: Long) {
        return monitoredSiteService.delete(id)
    }


}