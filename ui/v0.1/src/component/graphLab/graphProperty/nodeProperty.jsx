/**
 * Copyright (C) 2018 Mao Ito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../../css/structure.css';
import $ from 'jquery';
import ReactAutocomplete from 'react-autocomplete';

const menuStyleContent= {
  borderRadius: '3px',
  boxShadow: '0 2px 12px rgba(0, 0, 0, 0.1)',
  background: 'rgba(255, 255, 255, 0.9)',
  padding: '2px 0',
  fontSize: '90%',
  position: 'static',
  width: '100%',
  overflow: 'auto',
};

export default class NodeProperty extends React.Component<Props, {}> {

   constructor(props) {
        super(props);
        this.setInfo = this.setInfo.bind(this);
        this.deleteProperty = this.deleteProperty.bind(this);

       this.state = {
            propAutoCompletedValue: this.props.name,
            propItems: this.props.modelparameter
       }
   }

   componentDidMount(){
        this.refs.propName.value = this.props.name;
        this.refs.propValue.value = this.props.value;
   }

   setInfo(name, value){
     this.setState({
        propAutoCompletedValue: name
     });

     this.refs.provValue.value = value;
   }

   deleteProperty(){
        this.props.deleteCallBack(this.props.indexKey);
   }

   render() {
        return <div className={styles.nodeProp}>
                    <span className={styles.nodePropDelete} onClick={this.deleteProperty}><img src="./../icon/mono_icons/minus32.png" className={styles.icon}/></span>
                    <ReactAutocomplete
                        ref="propName"
                        className={styles.nodePropName}
                              items={this.state.propItems}
                            shouldItemRender={(item, value) => item.label.toLowerCase().indexOf(value.toLowerCase()) > -1}
                            getItemValue={item => item.label}
                            menuStyle={menuStyleContent}
                            renderItem={(item, highlighted) =>
                              <div
                                key={item.id}
                                style={{ backgroundColor: highlighted ? '#eee' : 'transparent', "zIndex": 2000}}
                              >
                                {item.label}
                              </div>
                            }
                            value={this.state.propAutoCompletedValue}
                            onChange={e => this.setState({ propAutoCompletedValue: e.target.value })}
                            onSelect={value => this.setState({ propAutoCompletedValue: value })}
                    />
                    <input className={styles.nodePropValue} ref="propValue" type="text" />
               </div>
   }
}