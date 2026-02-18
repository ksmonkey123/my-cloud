package ch.awae.mycloud.module.shortener

import ch.awae.mycloud.common.*
import org.springframework.data.repository.*
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import java.net.*

@RestController
class LinkResolutionController(private val repository: ShortLinkRepository) {

    @GetMapping("/s/{id}")
    fun resolve(@PathVariable id: String): ResponseEntity<Unit> {
        val link = repository.findByIdOrNull(id) ?: throw ResourceNotFoundException("/s/$id")
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(link.targetUrl)).build()
    }

}