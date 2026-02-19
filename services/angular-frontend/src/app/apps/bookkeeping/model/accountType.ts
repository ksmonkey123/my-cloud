export enum AccountType {
  ASSET = "ASSET",
  LIABILITY = "LIABILITY",
  EXPENSE = "EXPENSE",
  INCOME = "INCOME",
}

export class AccountTypeUtil {
  public static iconForType(type: AccountType): string {
    switch (type) {
      case AccountType.ASSET:
        return 'savings'
      case AccountType.LIABILITY:
        return 'credit_card'
      case AccountType.INCOME:
        return 'trending_up'
      case AccountType.EXPENSE:
        return 'trending_down'
    }
  }
}
