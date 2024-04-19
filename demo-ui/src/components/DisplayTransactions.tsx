import {FunctionComponent} from 'react';
import {TransactionResult} from '../services/listTransactions';

interface TxProps {
  transactions: Array<TransactionResult>
}

const SingleTransaction: FunctionComponent<TransactionResult> =  ({address, category, amount, time}) => {
  return <>
    <div>{address}</div>
    <div className='justify-self-center'>{category}</div>
    <div className='justify-self-end'>{amount.toString()} BTC</div>
    <div className='justify-self-end'>{new Date(Number(time) * 1000).toLocaleString("en-US", {month: "short", day: "numeric", hour: "numeric", minute: "numeric"})}</div>
  </>
}

const DisplayTransactions: FunctionComponent<TxProps> =  ({transactions}) => {
  return <div className='flex flex-col'>
    <div className='grid grid-cols-4 border-b'>
      <div className='font-bold text-sm'>Address</div>
      <div className='font-bold text-sm justify-self-center'>Category</div>
      <div className='font-bold text-sm justify-self-end'>Amount</div>
      <div className='font-bold text-sm justify-self-end'>Date</div>
    </div>
    <div className='grid grid-cols-4 gap-y-2'>
      {
        transactions.map((tx, idx) => <SingleTransaction key={idx} {...tx}/>)
      }
    </div>
  </div>
}

export default DisplayTransactions