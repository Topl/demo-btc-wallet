import axios from "axios"

export interface Balances {
    trusted: number
    untrustedPending: number
    immature: number,
}

const getBalances: (wallet: String) => Promise<Balances> = async (wallet: String) => {
    return axios
    .get(`api/getBalances/${wallet}`)
    .then(({data}) => Promise.resolve(data))
    .catch(e => {
        console.error(e)
        return Promise.reject(e)
    })
}    

export default getBalances