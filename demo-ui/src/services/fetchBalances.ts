import axios from "axios"

interface Balances {
    trusted: string
    untrustedPending: string
    immature: bigint,
}

const getBalances: (wallet: String) => Promise<Balances> = async (wallet: String) => {
    return axios
    .post("api/getBalances", {walletName: wallet})
    .then(({data: {trusted, untrustedPending, immature}}) => Promise.resolve({trusted, untrustedPending, immature}))
    .catch(e => {
        console.error(e)
        return Promise.reject(e)
    })
}    

export default getBalances