import {FunctionComponent} from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import Card from 'react-bootstrap/Card';
import { FormEventHandler } from 'react';
import getPubKey from '../services/getPubKey';
import storeAddress from '../services/storeAddress';
import { toast } from 'react-toastify';
import { useState } from 'react';

interface WalletProps {
  walletName: string
}

const GetPubKey: FunctionComponent<WalletProps> =  ({walletName}) => {
  const [pubKey, setPubKey] = useState<string>("")
  const [idx, setIdx] = useState<number>(0)

  const handleGenerate: FormEventHandler<HTMLFormElement> = event => {
    event.preventDefault();
    const form = event.currentTarget;
    const formData = new FormData(form);
    console.log(form)
    toast.promise(getPubKey(formData.get('fromWallet') as string), {
      pending: "Generating Public Key...",
      success: "Public Key generated successfully",
      error: "Failed to generate Public Key"
    })
    .then((res) => {
      setIdx(res.idx)
      setPubKey(res.pubKey)
    }, () => {})
  }

  const handleStore: FormEventHandler<HTMLFormElement> = event => {
    event.preventDefault();
    const form = event.currentTarget;
    const formData = new FormData(form);
    toast.promise(storeAddress({
      address: formData.get('address') as string,
      idx: idx
    }), {
      pending: "Storing information...",
      success: "Pub key and Address association stored successfully",
      error: "Failed to store information"
    })
    .then(() => {}, () => {})
  }
  

  return <div>
    <h1 className='py-4 text-center'>Initialize Session</h1>
    <Card className='w-1/2 m-auto'>
      <Card.Header className='!bg-slate-100'>{pubKey === "" ? "Generate Public Key": "Store Address"}</Card.Header>
      <Card.Body>
        <Form onSubmit={handleGenerate}>
          <Form.Group className="mb-3" controlId="walletName" hidden>
            <Form.Label>Local Wallet</Form.Label>
            <Form.Control type="text" value={walletName} name='fromWallet' readOnly/>
            <Form.Text className="text-muted">
              The wallet where the BTC will be sent from.
            </Form.Text>
          </Form.Group>
          <Form.Group className={"mb-3 " + (pubKey === "" ? "hidden" : "")} controlId="pubKey">
            <Form.Label>Public Key</Form.Label>
            <Form.Control type="text" name='pubKey' value={pubKey} disabled/>
            <Form.Text className="text-muted">
              The public key used to start a peg-in session.
            </Form.Text>
          </Form.Group>
          {
            pubKey === "" ? <Button variant="primary" type="submit" className="w-100">Generate</Button> : <></>
          }
        </Form>
        {
          pubKey === "" ? <></> : (
            <Form onSubmit={handleStore}>
              <Form.Group className="mb-3" controlId="idx" hidden>
                <Form.Label>Index</Form.Label>
                <Form.Control type="text" value={idx} name='idx' readOnly/>
                <Form.Text className="text-muted">
                  The index used to generate the public key
                </Form.Text>
              </Form.Group>
              <Form.Group className="mb-3 " controlId="address">
                <Form.Label>Escrow Address</Form.Label>
                <Form.Control type="text" name='address' placeholder='bcrt1...' required/>
                <Form.Text className="text-muted">
                  The peg-in escrow address that encumbers the public key.
                </Form.Text>
              </Form.Group>
              <Button variant="primary" type="submit" className="w-100">Save</Button>
            </Form>
          )
        }
      </Card.Body>
    </Card>
  </div>
}

export default GetPubKey