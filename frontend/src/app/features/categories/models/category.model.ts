import { BudgetBucket } from "./budget-bucket.model"

export interface Category {
    id: string
    name: string
    budgetBucket: BudgetBucket
}