import axios from "axios"


export interface PubKeyResponse {
    pubKey: string
    idx: number
}

const getPublicKey: (wallet: String) => Promise<PubKeyResponse> = async (wallet: String) => {
    return axios
    .get(`api/getPk/${wallet}`)
    .then(resp => Promise.resolve(resp.data))
    .catch(e => {
        console.error(e)
        return Promise.reject(e)
    })
}    

export default getPublicKey