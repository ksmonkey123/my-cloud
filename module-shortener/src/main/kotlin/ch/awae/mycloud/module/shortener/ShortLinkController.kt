package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.auth.AuthInfo
import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@PreAuthorize("hasAuthority('shortener')")
@RequestMapping("/rest/shortener/links")
@RestController
class ShortLinkController(private val svc: ShortLinkService) {

    @GetMapping
    fun list(): List<ShortLinkDTO> = svc.listShortLinks(AuthInfo.username)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreationRequest): ShortLinkDTO = svc.createShortLink(request.targetUrl, AuthInfo.username)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) {
        svc.deleteShortLink(id, AuthInfo.username)
    }

    data class CreationRequest(
        val targetUrl: String
    )

}