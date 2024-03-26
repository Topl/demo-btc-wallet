import {FunctionComponent} from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Card from 'react-bootstrap/Card';
import Container from 'react-bootstrap/Container';

const CreateWallet: FunctionComponent =  () => {
  return <Container>
    <h1 className='py-4 text-center'>Create Wallet</h1>
    <Card className='col-8 offset-2'>
      <Card.Header>Create a New Wallet</Card.Header>
      <Card.Body>
        <Form>
          <Form.Group className="mb-3" controlId="walletName">
            <Form.Label>Wallet Name</Form.Label>
            <Form.Control type="text" placeholder="my-wallet" />
            <Form.Text className="text-muted">
              The name of the new wallet.
            </Form.Text>
          </Form.Group>
          <Button variant="primary" type="submit">
            Create
          </Button>
        </Form>
      </Card.Body>
    </Card>
      </Container>
}

export default CreateWallet