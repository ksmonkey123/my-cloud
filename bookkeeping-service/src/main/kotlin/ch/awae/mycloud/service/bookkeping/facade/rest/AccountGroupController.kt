package ch.awae.mycloud.service.bookkeping.facade.rest

import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.service.*
import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books/{bookId}/groups")
@PreAuthorize("hasAuthority('bookkeeping')")
class AccountGroupController(
    val service: BookService
) {

    @GetMapping
    fun listAll(@PathVariable bookId: Long): List<AccountGroupDto> {
        return service.getAccountGroups(bookId)
    }

    @PutMapping("/{groupId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun createGroup(
        @PathVariable bookId: Long,
        @PathVariable groupId: Int,
        @RequestBody request: CreateGroupRequest
    ): AccountGroupDto {
        return service.createAccountGroup(bookId, groupId, request)
    }

    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteGroup(
        @PathVariable bookId: Long,
        @PathVariable groupId: Int,
    ) {
        service.deleteAccountGroup(bookId, groupId)
    }
}

data class CreateGroupRequest(
    val title: String,
)
