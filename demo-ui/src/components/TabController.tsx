import Transfer from '../views/Transfer';
import Wallet from '../views/Wallet';
import Tab from 'react-bootstrap/Tab';
import SideBar from './SideBar';

function TabController() {
    return <Tab.Container defaultActiveKey="transfer">
    <SideBar/>
    <Tab.Content className='basis-11/12 '>
      <Tab.Pane eventKey="view"><Wallet/></Tab.Pane>
      <Tab.Pane eventKey="transfer"><Transfer/></Tab.Pane>
    </Tab.Content>
  </Tab.Container> 
  }
  
  export default TabController