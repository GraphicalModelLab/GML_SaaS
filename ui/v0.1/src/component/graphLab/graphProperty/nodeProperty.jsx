import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Draggable from 'react-draggable';
import * as styles from './../../../css/structure.css';


export default class NodeProperty extends React.Component<Props, {}> {

   constructor(props) {
        super(props);
        this.setInfo = this.setInfo.bind(this);
   }

   componentDidMount(){
        this.refs.propName.value = this.props.name;
        this.refs.propValue.value = this.props.value;
   }
   setInfo(name, value){
     this.refs.propName.value = name;
     this.refs.provValue.value = value;
   }

   render() {
        return <div className={styles.nodeProp}>
                    <input className={styles.nodePropName} ref="propName" type="text" />
                    <input className={styles.nodePropValue} ref="propValue" type="text" />
               </div>
   }
}