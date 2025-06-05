package ch.awae.mycloud.service.bookkeping.service

import ch.awae.mycloud.*
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
    private val accountRepository: BookAccountRepository,
) {

    /**
     * @throws ResourceNotFoundException if book does not exist or ist not accessible by user
     */
    fun getBook(bookId: Long) = bookRepository.findByIdAndUsername(bookId, AuthInfo.username!!)
        ?: throw ResourceNotFoundException("/books/$bookId")

    fun getBooks(includeClosed: Boolean): List<BookSummaryDto> {
        val rawList = if (includeClosed) {
            bookRepository.findAllByUsername(AuthInfo.username!!)
        } else {
            bookRepository.findOpenByUsername(AuthInfo.username!!)
        }

        return rawList.map(::BookSummaryDto)
            .sortedByDescending { it.openingDate }
    }

    fun getBookDetails(bookId: Long): BookDto {
        return BookDto(
            book = BookSummaryDto(getBook(bookId)),
            groups = getAccountGroups(bookId)
        )
    }

    fun createBook(dto: CreateBookRequest): BookSummaryDto {
        val createdBook = bookRepository.save(
            Book(
                username = AuthInfo.username!!,
                title = dto.title,
                openingDate = dto.openingDate,
                closingDate = dto.closingDate,
                description = dto.description,
                closed = false,
            )
        )
        return BookSummaryDto(createdBook)
    }

    fun editBook(bookId: Long, request: EditBookRequest): BookSummaryDto {
        val book = getBook(bookId)

        book.title = request.title
        book.description = request.description?.trim()?.takeIf { it.isNotBlank() }
        book.closed = request.closed

        return BookSummaryDto(book)
    }

    fun getAccountGroups(bookId: Long): List<AccountGroupDto> {
        val book = getBook(bookId)
        return book.accountGroups.map { AccountGroupDto(it) }.sortedBy { it.groupNumber }
    }

    fun createOrEditAccountGroup(bookId: Long, groupId: Int, dto: PersistGroupRequest): AccountGroupDto {
        val book = getBook(bookId)
        val createdGroup = groupRepository.getByBookAndGroupNumber(book, groupId)
            ?.apply {
                this.title = dto.title
                this.locked = dto.locked
            }
            ?: groupRepository.save(AccountGroup(book, groupId, dto.title, locked = dto.locked))

        return AccountGroupDto(createdGroup)
    }

    fun deleteAccountGroup(bookId: Long, groupId: Int) {
        val book = getBook(bookId)
        val accountGroup = groupRepository.getByBookAndGroupNumber(book, groupId)
            ?: throw ResourceNotFoundException("/books/$bookId/groups/$groupId")

        if (accountGroup.locked) {
            throw InvalidRequestException("/books/$bookId/groups/$groupId is locked")
        }

        // TODO verify no account in group has any booking records
        groupRepository.delete(accountGroup)
    }

    fun getAccounts(bookId: Long): List<AccountSummaryDto> {
        val book = getBook(bookId)

        return accountRepository.listAllOfBook(book).map { AccountSummaryDto(it) }
    }

    fun getAccount(bookId: Long, accountId: AccountId): BookAccount {
        return accountRepository.findByIdentifier(
            book = getBook(bookId),
            groupNumber = accountId.groupNumber,
            accountNumber = accountId.accountNumber,
        ) ?: throw ResourceNotFoundException("/books/$bookId/accounts/$accountId")
    }

    fun createOrEditAccount(bookId: Long, accountId: AccountId, request: PersistAccountRequest): AccountSummaryDto {
        val book = getBook(bookId)

        val group = book.accountGroups.find { it.groupNumber == accountId.groupNumber }
            ?: throw ResourceNotFoundException("/books/$bookId/groups/${accountId.groupNumber}")

        if (group.locked) {
            throw InvalidRequestException("/books/$bookId/groups/${accountId.groupNumber} is locked")
        }

        val existingAccount = accountRepository.findByGroupAndAccountNumber(group, accountId.accountNumber)

        val accountToSave = if (existingAccount != null) {
            // update existing account
            existingAccount.title = request.title
            existingAccount.description = request.description
            existingAccount.locked = request.locked
            existingAccount
        } else {
            // create new account
            BookAccount(
                accountNumber = accountId.accountNumber,
                accountGroup = group,
                accountType = request.accountType
                    ?: throw InvalidRequestException("account creation requires an accountType"),
                title = request.title,
                description = request.description,
                locked = request.locked,
            )
        }

        return AccountSummaryDto(accountRepository.save(accountToSave))
    }

    fun deleteAccount(bookId: Long, accountId: AccountId) {
        val account = getAccount(bookId, accountId)

        if (account.accountGroup.locked) {
            throw InvalidRequestException("/books/$bookId/groups/${accountId.groupNumber} is locked")
        }
        if (account.locked) {
            throw InvalidRequestException("/books/$bookId/accounts/$accountId is locked")
        }

        // TODO verify account has no bookings
        accountRepository.delete(account)
    }

}