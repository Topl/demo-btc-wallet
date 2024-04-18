import {FunctionComponent} from 'react';
import Card from 'react-bootstrap/Card';

const Wallet: FunctionComponent =  () => {
  return <div className='flex flex-col space-y-4 mt-4'>
    <h1 className='text-center'>Wallet Details</h1>
    <div>
      <Card className='w-1/2 m-auto'>
        <Card.Header className='!bg-slate-100'>Quantity</Card.Header>
        <Card.Body>

        </Card.Body>
      </Card>
    </div>
    <div>
      <Card className='w-1/2 m-auto'>
        <Card.Header className='!bg-slate-100'>Recent Transactions</Card.Header>
        <Card.Body>

        </Card.Body>
      </Card>
    </div>
  </div>
}

export default Wallet