import axios from "axios"

interface TransferData {
    fromWallet: string
    toAddress: string
    quantity: bigint,
    transferType: String,
    pegInOptions?: PegInOptions
}

interface PegInOptions {
    sessionId: String
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