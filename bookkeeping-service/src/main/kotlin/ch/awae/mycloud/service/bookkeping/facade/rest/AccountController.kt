package ch.awae.mycloud.service.bookkeping.facade.rest

import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.model.*
import ch.awae.mycloud.service.bookkeping.service.*
import org.springframework.http.*
import org.springframework.security.access.prepost.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books/{bookId}/accounts")
@PreAuthorize("hasAuthority('bookkeeping')")
class AccountController(private val service: BookService) {

    @GetMapping
    fun listAccounts(@PathVariable bookId: Long): List<AccountSummaryDto> {
        return service.getAccounts(bookId)
    }

    @PutMapping("/{accountId}")
    fun createOrUpdateAccount(
        @PathVariable bookId: Long,
        @PathVariable accountId: String,
        @RequestBody request: EditAccountRequest
    ): AccountSummaryDto {
        return service.createOrEditAccount(bookId, AccountId.of(accountId), request)
    }

    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAccount(
        @PathVariable bookId: Long,
        @PathVariable accountId: String,
    ) {
        service.deleteAccount(bookId, AccountId.of(accountId))
    }

}

data class EditAccountRequest(
    val title: String,
    val description: String?,
    val accountType: AccountType?
)