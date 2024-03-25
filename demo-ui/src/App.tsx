import './App.css'
import Transfer from './views/Transfer';
import Col from 'react-bootstrap/Col';
import Nav from 'react-bootstrap/Nav';
import Row from 'react-bootstrap/Row';
import Tab from 'react-bootstrap/Tab';


function App() {
  return (
    <div className='container-fluid w-100 m-0 p-0 bg-white'>
      <Row className='w-100 m-0 border-bottom bg-light'>
        <Col xs={3} className='p-3 lead logo-text m-auto'>Demo App</Col>
        <Col className='p-3 border-start' id="topbar">
          <Nav activeKey="btc" className="flex-row list-group">
            <Nav.Link eventKey="bridge" className=''>Bridge</Nav.Link>
            <Nav.Link eventKey="btc">Demo BTC Wallet</Nav.Link>
            <Nav.Link eventKey="topl" >Demo Topl Wallet</Nav.Link>
          </Nav>
        </Col>
      </Row>
      <Tab.Container defaultActiveKey="transfer">
        <Row className='vh-100 m-0'>
          <Col xs={3} className='p-0' id="sidebar">
            <Nav className="flex-column">
                <Nav.Link eventKey="transfer" className='p-3'>Transfer BTC</Nav.Link>
            </Nav>
          </Col>
          <Col className='border-start'>
            <Tab.Content>
              <Tab.Pane eventKey="transfer"><Transfer/></Tab.Pane>
            </Tab.Content>
          </Col>
        </Row>
      </Tab.Container>
    </div>
  )
}

export default App
