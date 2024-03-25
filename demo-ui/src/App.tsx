import './App.css'
import Transfer from './views/Transfer';
import Col from 'react-bootstrap/Col';
import Nav from 'react-bootstrap/Nav';
import Row from 'react-bootstrap/Row';
import Tab from 'react-bootstrap/Tab';


function App() {
  return (
    <div className='container-fluid w-100 m-0 p-0'>
    <Row>
      <Col xs={3}>Demo App</Col>
      <Col>Nav Bar</Col>
    </Row>
    <Tab.Container defaultActiveKey="transfer">
      <Row className='vh-100'>
        <Col xs={3}>
          <Nav className="flex-column">
            <Nav.Item>
              <Nav.Link eventKey="transfer">Transfer BTC</Nav.Link>
            </Nav.Item>
          </Nav>
        </Col>
        <Col>
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
