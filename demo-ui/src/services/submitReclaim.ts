import axios from "axios"

interface ReclaimData {
    toWallet: string
    fromAddress: string
}

const submitReclaim = async (data: ReclaimData) => {
    return axios
    .post("api/reclaim", data)
    .then(resp => Promise.resolve(resp.data))
    .catch(e => {
        console.error(e)
        return Promise.reject(e)
    })
}    

export default submitReclaim