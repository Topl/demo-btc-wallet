import Nav from 'react-bootstrap/Nav';

function TopBar() {
    const classes = "select-none p-4 !text-slate-700 [&.active]:font-bold [&.active]:pointer-events-none"
    return <Nav activeKey="btc" defaultActiveKey="btc" className='basis-11/12 items-end'>
        <Nav.Link eventKey="bridge" className={classes}>Bridge</Nav.Link>
        <Nav.Link eventKey="btc" className={classes}>Demo BTC Wallet</Nav.Link>
        <Nav.Link eventKey="topl" className={classes}>Demo Topl Wallet</Nav.Link>
    </Nav>
  }
  
  export default TopBar