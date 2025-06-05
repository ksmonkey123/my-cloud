package ch.awae.mycloud.service.shortener.facade.rest

import ch.awae.mycloud.*
import ch.awae.mycloud.service.shortener.model.*
import org.springframework.data.repository.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
class LinkResolutionController(private val repository: ShortLinkRepository) {

    @GetMapping("/s/{id}")
    fun resolve(@PathVariable id: String): ResponseEntity<Unit> {
        val link = repository.findByIdOrNull(id) ?: throw ResourceNotFoundException("/s/$id")
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(link.targetUrl)).build()
    }

}