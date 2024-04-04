import Nav from 'react-bootstrap/Nav';

function SideBar() {
    const classes = "select-none p-3 hover:bg-slate-200 !text-slate-700 [&.active]:bg-slate-300 [&.active]:font-semibold [&.active]:pointer-events-none"
    return <Nav className="basis-1/6 flex-column border-r">
        <Nav.Link eventKey="view" className={classes}>View Wallet</Nav.Link>
        <Nav.Link eventKey="transfer" className={classes}>Transfer BTC</Nav.Link>
    </Nav>
  }
  
  export default SideBar