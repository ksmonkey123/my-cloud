package ch.awae.mycloud.ytdl

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/ytdl/jobs")
@PreAuthorize("hasAuthority('ytdl')")
class JobRestController {



}