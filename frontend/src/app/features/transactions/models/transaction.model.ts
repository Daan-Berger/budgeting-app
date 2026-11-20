export interface Transaction {
    id: string
    date: string
    amount: number
    counterparty: string
    counterpartyIban: string | null
    description: string | null
    categoryId: string | null
    categoryName: string | null
}
