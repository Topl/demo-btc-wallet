import {FunctionComponent} from 'react';
import {Balances} from '../services/fetchBalances';

interface BalanceProps {
  btc: bigint
  label: string
}

const SingleBalance: FunctionComponent<BalanceProps> =  ({btc, label}) => {
  return <div className='flex flex-col items-center'>
    <strong>{label}</strong>
    <span>{btc.toLocaleString("en")} BTC</span>
    <span className='text-xs font-thin'>&#x28;{(btc*100000000n).toLocaleString("en")} sats&#x29;</span>
  </div>
}

const DisplayBalance: FunctionComponent<Balances> =  ({immature, untrustedPending, trusted}) => {
  return <div className='flex justify-around'>
    <SingleBalance label="Trusted" btc={trusted}/>
    <SingleBalance label="Immature" btc={immature}/>
    <SingleBalance label="Untrusted or Pending" btc={untrustedPending}/>
  </div>
}

export default DisplayBalance