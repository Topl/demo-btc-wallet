import {FunctionComponent} from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Card from 'react-bootstrap/Card';

const Transfer: FunctionComponent =  () => {
  return <div>
    <h1>Transfer BTC</h1>
    <Card>
      <Card.Header>Transfer BTC for Peg-in</Card.Header>
      <Card.Body>
        <Form>
          <Form.Group className="mb-3" controlId="recipientAddress">
            <Form.Label>Address</Form.Label>
            <Form.Control type="text" placeholder="bcrt1q58..." />
            <Form.Text className="text-muted">
              The address to send BTC to.
            </Form.Text>
          </Form.Group>

          <Form.Group className="mb-3" controlId="transferAmount">
            <Form.Label>Quantity</Form.Label>
            <Form.Control type="text" placeholder="1234" />
            <Form.Text className="text-muted">
              The amount of BTC to send to the recipient in Satoshis. 1 BTC = 100,000,000 Satoshis.
            </Form.Text>
          </Form.Group>
          <Button variant="primary" type="submit">
            Send Funds
          </Button>
        </Form>
      </Card.Body>
    </Card>
      </div>
}

export default Transfer