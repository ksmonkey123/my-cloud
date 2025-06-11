package ch.awae.mycloud.module.shortener

import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@PreAuthorize("hasAuthority('shortener')")
@RequestMapping("/rest/shortener/links")
@RestController
class ShortLinkController(private val svc: ShortLinkService) {

    @GetMapping
    fun list(): List<ShortLinkDTO> = svc.listShortLinks()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreationRequest): ShortLinkDTO = svc.createShortLink(request.targetUrl)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) {
        svc.deleteShortLink(id)
    }

    data class CreationRequest(
        val targetUrl: String
    )

}