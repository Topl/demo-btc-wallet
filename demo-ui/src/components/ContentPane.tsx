import Transfer from '../views/Transfer';
import LoadWallet from '../views/LoadWallet';
import CreateWallet from '../views/CreateWallet';
import Tab from 'react-bootstrap/Tab';

function ContentPane() {
    return <Tab.Content className='basis-11/12 '>
      <Tab.Pane eventKey="create"><CreateWallet/></Tab.Pane>
      <Tab.Pane eventKey="load"><LoadWallet/></Tab.Pane>
      <Tab.Pane eventKey="transfer"><Transfer/></Tab.Pane>
    </Tab.Content>
  }
  
  export default ContentPane