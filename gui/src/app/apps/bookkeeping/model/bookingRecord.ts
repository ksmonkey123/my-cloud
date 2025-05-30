import Big from "big.js";

export interface BookingRecord {
  id: number,
  date: Date,
  tag?: string,
  text: string,
  description?: string,
  amount: Big,
  credits: BookingMovement[],
  debits: BookingMovement[],
}

export interface BookingMovement {
  accountId: string,
  amount: Big,
}

