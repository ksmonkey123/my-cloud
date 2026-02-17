export interface BookingRecord {
  id: number,
  date: Date,
  tag?: string,
  text: string,
  description?: string,
  amount: number,
  credits: BookingMovement[],
  debits: BookingMovement[],
}

export interface BookingMovement {
  accountId: string,
  amount: number,
}

