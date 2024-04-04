import axios from "axios"

interface TransferData {
    fromWallet: string
    toAddress: string
    quantity: bigint
}

const submitTransfer = async (data: TransferData) => {
    return axios
    .post("api/transfer", data)
    .then(resp => Promise.resolve(resp.data))
    .catch(e => {
        console.error(e)
        return Promise.reject(e)
    })
}    

export default submitTransfer