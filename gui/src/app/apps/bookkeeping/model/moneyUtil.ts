import {AccountType} from "./accountType";
import {AccountSummary} from "./book";

export class MoneyUtil {
  public static formatForDisplay(amount: number): string {
    let rawString = amount.toFixed(2)
    if (rawString.endsWith('.00')) {
      return rawString.split('.')[0] + '.--'
    } else {
      return rawString
    }
  }

  public static formatAccountBalanceForDisplay(account: AccountSummary): string {
    if ((account.accountType === AccountType.INCOME) || (account.accountType === AccountType.LIABILITY)) {
      return MoneyUtil.formatForDisplay(-account.balance)
    } else {
      return MoneyUtil.formatForDisplay(account.balance)
    }
  }
}
