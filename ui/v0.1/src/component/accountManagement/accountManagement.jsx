import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';

export default class AccountManagement extends React.Component<Props, {}> {

    constructor(props) {
        super(props);

        this.accountRoleChange = this.accountRoleChange.bind(this);
    }

    accountRoleChange(e){
        e.preventDefault();

        this.refs.loading.openModal();

        var data = {
            companyid: auth.getCompanyid(),
            role: auth.getRole(),
            userid:auth.getUserid(),
            token: auth.getToken(),
            changingUserid: this.refs.roleEmail.value,
            changingUserCompany: this.refs.companyCode.value,
            newRole: $('input[name="newRole"]:checked').val(),
            code:10
        };

        let parent = this;

        $.ajax({
            type:"post",
            url:"../commonModules/php/modules/Auth.php/auth/changeRole",
            data:JSON.stringify(data),
            contentType: 'application/json',
            dataType: "json",
            success: function(json_data) {
               parent.refs.message.innerText = "(権限を"+data.newRole+"へ変更しました)"

               setTimeout(function(){
                parent.refs.message.innerText = ""
               }, 1000);

               parent.refs.loading.closeModal();
            },
            error: function (request, status, error) {
                alert(request.responseText);
            },
            complete: function() {
            }
        });

    }
    render() {
        return (
            <div>
               <div className={styles.accountRoleChange}>
                <div>
                    <div className={styles.accountRoleChangeTitle}> 権限変更 </div>
                    <div className={styles.accountRoleChangeItemBox}>
                        <div className={styles.accountRoleChangeItemName}>
                            Company Code と　E-mailアドレスを入力し、”一般”か”管理者”の権限どちらかをお選びください。
                        </div>
                        <input type="text" ref="companyCode" placeholder="company code"/>
                        <input type="text" ref="roleEmail" placeholder="E-mailアドレス"/>
                        <input type="radio" name="newRole" value="none"/> 一般
                        <input type="radio" name="newRole" value="administrator"/> 管理者
                    </div>
                </div>
                <div className={styles.accountRoleChangeButton}>
                 <div className={styles.accountRoleChangeButtonBox}><div onClick={this.accountRoleChange.bind(this)} width="200"><img src="../icon/mono_icons/exchange32.png" className={styles.icon}/>変更 <span ref="message"></span></div></div>
                </div>

                 <Loading ref="loading"/>
               </div>
           </div>
           )
    }
}