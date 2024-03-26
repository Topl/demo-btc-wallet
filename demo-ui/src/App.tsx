import './App.css'
import Transfer from './views/Transfer';
import LoadWallet from './views/LoadWallet';
import CreateWallet from './views/CreateWallet';
import Container from 'react-bootstrap/Container';
import Col from 'react-bootstrap/Col';
import Nav from 'react-bootstrap/Nav';
import Row from 'react-bootstrap/Row';
import Tab from 'react-bootstrap/Tab';


function App() {
  return (
    <Container className='w-100 m-0 p-0 bg-white top-0 h-100 vh-100' fluid>
      <Row className='w-100 m-0 border-bottom bg-light'>
        <Col xs={2} className='p-3 lead logo-text m-auto'>Demo App</Col>
        <Col className='p-3 border-start' id="topbar">
          <Nav activeKey="btc">
            <Nav.Link eventKey="bridge">Bridge</Nav.Link>
            <Nav.Link eventKey="btc">Demo BTC Wallet</Nav.Link>
            <Nav.Link eventKey="topl" >Demo Topl Wallet</Nav.Link>
          </Nav>
        </Col>
      </Row>
      <Tab.Container defaultActiveKey="transfer">
        <Row className='m-0'>
          <Col xs={2} className='p-0' id="sidebar">
            <Nav className="flex-column">
                <Nav.Link eventKey="create" className='p-3'>Create Wallet</Nav.Link>
                <Nav.Link eventKey="load" className='p-3'>Load Wallet</Nav.Link>
                <Nav.Link eventKey="transfer" className='p-3'>Transfer BTC</Nav.Link>
            </Nav>
          </Col>
          <Col className='border-start'>
            <Tab.Content>
              <Tab.Pane eventKey="create"><CreateWallet/></Tab.Pane>
              <Tab.Pane eventKey="load"><LoadWallet/></Tab.Pane>
              <Tab.Pane eventKey="transfer"><Transfer/></Tab.Pane>
            </Tab.Content>
          </Col>
        </Row>
      </Tab.Container>
    </Container>
  )
}

export default App
