export interface Identifiable {
  id: string
}

export interface ProcessableItem extends Identifiable {
  processing: Boolean
}
