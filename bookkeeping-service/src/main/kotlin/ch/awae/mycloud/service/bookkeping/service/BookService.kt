package ch.awae.mycloud.service.bookkeping.service

import ch.awae.mycloud.*
import ch.awae.mycloud.audit.*
import ch.awae.mycloud.auth.*
import ch.awae.mycloud.service.bookkeping.dto.*
import ch.awae.mycloud.service.bookkeping.facade.rest.*
import ch.awae.mycloud.service.bookkeping.model.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class BookService(
    private val bookRepository: BookRepository,
    private val groupRepository: AccountGroupRepository,
    private val accountRepository: AccountRepository,
) {

    /**
     * @throws ResourceNotFoundException if book does not exist or ist not accessible by user
     */
    fun getBook(bookId: Long) = bookRepository.findByIdAndUsername(bookId, AuthInfo.username!!)
        ?: throw ResourceNotFoundException("/books/$bookId")

    fun getBooks(): List<BookSummaryDto> {
        return bookRepository.findByUsername(AuthInfo.username!!)
            .map(::BookSummaryDto)
            .sortedByDescending { it.openingDate }
    }

    fun getBookDetails(bookId: Long): BookDto {
        return BookDto(
            book = BookSummaryDto(getBook(bookId)),
            groups = getAccountGroups(bookId)
        )
    }

    @AuditLog
    fun createBook(dto: CreateBookRequest): BookSummaryDto {
        val createdBook = bookRepository.save(
            Book(
                AuthInfo.username!!,
                dto.title,
                dto.openingDate,
                dto.closingDate,
                dto.description
            )
        )
        return BookSummaryDto(createdBook)
    }

    @AuditLog
    fun editBook(bookId: Long, request: EditBookRequest): BookSummaryDto {
        val book = getBook(bookId)

        book.title = request.title
        book.description = request.description?.trim()?.takeIf { it.isNotBlank() }

        return BookSummaryDto(book)
    }

    fun getAccountGroups(bookId: Long): List<AccountGroupDto> {
        val book = getBook(bookId)
        return book.accountGroups.map { AccountGroupDto(it) }.sortedBy { it.groupNumber }
    }

    @AuditLog
    fun createAccountGroup(bookId: Long, groupId: Int, dto: CreateGroupRequest): AccountGroupDto {
        val book = getBook(bookId)
        val createdGroup = groupRepository.save(AccountGroup(book, groupId, dto.title))
        return AccountGroupDto(createdGroup)
    }

    @AuditLog
    fun deleteAccountGroup(bookId: Long, groupId: Int) {
        val book = getBook(bookId)
        val accountGroup = groupRepository.getByBookAndGroupNumber(book, groupId)
            ?: throw ResourceNotFoundException("/books/$bookId/groups/$groupId")

        // TODO verify no account in group has any booking records
        groupRepository.delete(accountGroup)
    }

    fun getAccounts(bookId: Long): List<AccountSummaryDto> {
        val book = getBook(bookId)

        return accountRepository.listAllOfBook(book).map { AccountSummaryDto(it) }
    }

    fun getAccount(bookId: Long, accountId: AccountId): Account {
        return accountRepository.findByIdentifier(
            book = getBook(bookId),
            groupNumber = accountId.groupNumber,
            accountNumber = accountId.accountNumber,
        ) ?: throw ResourceNotFoundException("/books/$bookId/accounts/$accountId")
    }

    @AuditLog
    fun createOrEditAccount(bookId: Long, accountId: AccountId, request: EditAccountRequest): AccountSummaryDto {
        val book = getBook(bookId)

        val group = book.accountGroups.find { it.groupNumber == accountId.groupNumber }
            ?: throw ResourceNotFoundException("/books/$bookId/groups/${accountId.groupNumber}")

        val existingAccount = accountRepository.findByGroupAndAccountNumber(group, accountId.accountNumber)

        val accountToSave = if (existingAccount != null) {
            // update existing account
            existingAccount.title = request.title
            existingAccount.description = request.description
            existingAccount
        } else {
            // create new account
            Account(
                accountNumber = accountId.accountNumber,
                accountGroup = group,
                accountType = request.accountType
                    ?: throw InvalidRequestException("account creation requires an accountType"),
                title = request.title,
                description = request.description,
            )
        }

        return AccountSummaryDto(accountRepository.save(accountToSave))
    }

    @AuditLog
    fun deleteAccount(bookId: Long, accountId: AccountId) {
        val account = getAccount(bookId, accountId)
        // TODO verify account has no bookings
        accountRepository.delete(account)
    }

}