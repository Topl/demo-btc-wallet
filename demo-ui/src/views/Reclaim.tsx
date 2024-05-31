import {FunctionComponent} from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Card from 'react-bootstrap/Card';
import { FormEventHandler } from 'react';
import submitReclaim from '../services/submitReclaim';
import { toast } from 'react-toastify';

const handleSubmit: FormEventHandler<HTMLFormElement> = event => {
  event.preventDefault();
  const form = event.currentTarget;
  const formData = new FormData(form);
  toast.promise(submitReclaim({
    toWallet: formData.get('toWallet') as string,
    fromAddress: formData.get('fromAddress') as string
  }), {
    pending: "Submitting reclaim request...",
    success: "Reclaim request submitted successfully",
    error: "Failed to submit reclaim request"
  })
  .then(() => form.reset(), () => {})
}


interface WalletProps {
  walletName: string
}

const Reclaim: FunctionComponent<WalletProps> =  ({walletName}) => {

  return <div>
    <h1 className='py-4 text-center'>Reclaim Transferred BTC</h1>
    <Card className='w-1/2 m-auto'>
      <Card.Header className='!bg-slate-100'>Reclaim BTC that was previously transferred</Card.Header>
      <Card.Body>
        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3" controlId="walletName" hidden>
            <Form.Label>Local Wallet</Form.Label>
            <Form.Control type="text" value={walletName} name='toWallet' readOnly/>
            <Form.Text className="text-muted">
              The wallet where the BTC will be sent from.
            </Form.Text>
          </Form.Group>
          <Form.Group className="mb-3" controlId="fromAddress">
            <Form.Label>Originating Address</Form.Label>
            <Form.Control type="text" placeholder="bcrt1q58..." name='fromAddress' required/>
            <Form.Text className="text-muted">
              The address where we are reclaiming BTC from. 
            </Form.Text>
          </Form.Group>
          <Button variant="primary" type="submit">
            Reclaim
          </Button>
        </Form>
      </Card.Body>
    </Card>
  </div>
}

export default Reclaim