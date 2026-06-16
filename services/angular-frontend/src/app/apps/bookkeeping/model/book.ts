import {BookSummary} from "./bookSummary";
import {AccountType} from "./accountType";

export interface Book {
  id: number
  summary: BookSummary
  groups: AccountGroup[]
}

export interface AccountGroup {
  groupNumber: number
  title: string
  locked: boolean
  hasTransactions: boolean
  accounts: AccountSummary[]
}

export interface AccountSummary {
  id: string
  title: string
  description?: string
  accountType: AccountType
  balance: number
  hasTransactions: boolean
  locked: boolean
}
