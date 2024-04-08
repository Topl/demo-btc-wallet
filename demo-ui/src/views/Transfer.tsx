import {FunctionComponent} from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Card from 'react-bootstrap/Card';
import { FormEventHandler } from 'react';
import submitTransfer from '../services/submitTransfer';
import { toast } from 'react-toastify';

const handleSubmit: FormEventHandler<HTMLFormElement> = event => {
  event.preventDefault();
  const form = event.currentTarget;
  const formData = new FormData(form);
  toast.promise(submitTransfer({
    fromWallet: formData.get('fromWallet') as string,
    toAddress: formData.get('toAddress') as string,
    quantity: BigInt(formData.get('quantity') as string),
  }), {
    pending: "Submitting transfer request...",
    success: "Transfer request submitted successfully",
    error: "Failed to submit transfer request"
  })
  .then(() => form.reset(), () => {})
}

const Transfer: FunctionComponent =  () => {
  return <div>
    <h1 className='py-4 text-center'>Transfer BTC</h1>
    <Card className='w-1/2 m-auto'>
      <Card.Header className='!bg-slate-100'>Transfer BTC for Peg-in from default local wallet</Card.Header>
      <Card.Body>
        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3" controlId="walletName" hidden>
            <Form.Label>Local Wallet</Form.Label>
            {/* Hardcoding wallet to default */}
            <Form.Control type="text" value="default" name='fromWallet' readOnly/>
            <Form.Text className="text-muted">
              The wallet where the BTC will be sent from.
            </Form.Text>
          </Form.Group>
          <Form.Group className="mb-3" controlId="recipientAddress">
            <Form.Label>Recipient Address</Form.Label>
            <Form.Control type="text" placeholder="bcrt1q58..." name='toAddress' required/>
            <Form.Text className="text-muted">
              The address to send BTC to.
            </Form.Text>
          </Form.Group>

          <Form.Group className="mb-3" controlId="transferAmount">
            <Form.Label>Quantity</Form.Label>
            <Form.Control type="number" placeholder="1234" name='quantity' min={0} required/>
            <Form.Text className="text-muted">
              The amount of BTC to send to the recipient in Satoshis. 1 BTC = 100,000,000 Satoshis.
            </Form.Text>
          </Form.Group>
          <Button variant="primary" type="submit">
            Send
          </Button>
        </Form>
      </Card.Body>
    </Card>
  </div>
}

export default Transfer