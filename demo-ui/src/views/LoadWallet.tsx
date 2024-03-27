import {FunctionComponent} from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Card from 'react-bootstrap/Card';

const LoadWallet: FunctionComponent =  () => {
  return <div>
    <h1 className='py-4 text-center'>Load Wallet</h1>
    <Card className='w-1/2 m-auto'>
      <Card.Header className='!bg-slate-100'>Load a Wallet</Card.Header>
      <Card.Body>
        <Form>
          <Form.Group className="mb-3" controlId="walletName">
            <Form.Label>Wallet</Form.Label>
            <Form.Select aria-label="Select Wallet">
              <option>Select Unloaded Wallet</option>
              <option value="wallet1">TestWallet</option>
              <option value="wallet2">dummy-wallet</option>
            </Form.Select>
            <Form.Text className="text-muted">
              The unloaded existing wallet to load.
            </Form.Text>
          </Form.Group>
          <Button variant="primary" type="submit">
            Load
          </Button>
        </Form>
      </Card.Body>
    </Card>
      </div>
}

export default LoadWallet