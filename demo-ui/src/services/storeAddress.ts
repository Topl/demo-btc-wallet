import axios from "axios"

interface AddressStoreData {
    address: string
    script: string
    idx: number
}

const storeAddress = async (data: AddressStoreData) => {
    return axios
    .post("api/storeAddress", data)
    .then(resp => Promise.resolve(resp.data))
    .catch(e => {
        console.error(e)
        return Promise.reject(e)
    })
}    

export default storeAddress