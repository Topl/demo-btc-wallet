import axios from "axios"

export interface TransactionResult {
    address: string
    category: string
    amount: number,
    time: number
}

const listTransactions: (wallet: String) => Promise<Array<TransactionResult>> = async (wallet: String) => {
    return axios
    .get(`api/listTransactions/${wallet}`)
    .then(({data}) => Promise.resolve(data))
    .catch(e => {
        console.error(e)
        return Promise.reject(e)
    })
}    

export default listTransactions