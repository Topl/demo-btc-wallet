import {FunctionComponent} from 'react';
import {Balances} from '../services/fetchBalances';

interface BalanceProps {
  sats: number
  label: string
}

const SingleBalance: FunctionComponent<BalanceProps> =  ({sats, label}) => {
  return <div className='flex flex-col items-center'>
    <strong>{label}</strong>
    <span>{(sats/100000000).toLocaleString("en")} BTC</span>
    <span className='text-xs font-thin'>&#x28;{sats.toLocaleString("en")} sats&#x29;</span>
  </div>
}

const DisplayBalance: FunctionComponent<Balances> =  ({immature, untrustedPending, trusted}) => {
  return <div className='flex justify-around'>
    <SingleBalance label="Trusted" sats={trusted}/>
    <SingleBalance label="Immature" sats={immature}/>
    <SingleBalance label="Untrusted or Pending" sats={untrustedPending}/>
  </div>
}

export default DisplayBalance