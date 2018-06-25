import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';

export default class AccountManagementIndividual extends React.Component<Props, {}> {

    constructor(props) {
        super(props);

        this.accountPasswordChange = this.accountPasswordChange.bind(this);
        this.removeAccount = this.removeAccount.bind(this);
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
                changingUserCompany: "",
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

    accountPasswordChange(e){
        e.preventDefault();

        this.refs.loading.openModal();

        var data = {
            companyid: auth.getCompanyid(),
            userid:auth.getUserid(),
            token: auth.getToken(),
            newPassword: this.refs.newPassword.value,
            code:10
        };

        let parent = this;

        if(this.refs.newPassword.value == this.refs.newPasswordConfirmation.value){
            $.ajax({
                type:"post",
                url:"../commonModules/php/modules/Auth.php/auth/changePassword",
                data:JSON.stringify(data),
                contentType: 'application/json',
                dataType: "json",
                success: function(json_data) {
                   parent.refs.messageChangePassword.innerText = "(パスワードを変更しました。)"

                   setTimeout(function(){
                    parent.refs.messageChangePassword.innerText = ""
                   }, 2000);

                   parent.refs.loading.closeModal();
                },
                error: function (request, status, error) {
                    alert(request.responseText);
                },
                complete: function() {
                }
            });
        }else{
            parent.refs.messageChangePassword.innerText = "(新しいパスワードと確認用のパスワードが一致しません。再度、入力をお願いします。)"

            setTimeout(function(){
                parent.refs.messageChangePassword.innerText = ""
            }, 2000);

            parent.refs.loading.closeModal();
        }
    }

    removeAccount(e){
        e.preventDefault();

        this.refs.loading.openModal();

        var data = {
                companyid: auth.getCompanyid(),
                userid:auth.getUserid(),
                token: auth.getToken(),
                deletedUserid: auth.getUserid(),
                code:10
        };

        let parent = this;

        $.ajax({
                type:"post",
                url:"../commonModules/php/modules/Auth.php/auth/removeAccount",
                data:JSON.stringify(data),
                contentType: 'application/json',
                dataType: "json",
                success: function(json_data) {
                   parent.refs.messageRemoveAccount.innerText = "(退会いたしました。再度入会するには、再度登録をお願い致します。)"

                   setTimeout(function(){
                    parent.refs.messageRemoveAccount.innerText = ""
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
                    <div className={styles.accountRoleChangeTitle}> パスワード変更 </div>
                    <div className={styles.accountRoleChangeItemBox}>
                        <div className={styles.accountRoleChangeItemName}>
                            古いパスワード <input type="password" ref="oldPassword" placeholder="古いパスワード"/>
                        </div>
                        <div className={styles.accountRoleChangeItemName}>
                            新しいパスワード <input type="password" ref="newPassword" placeholder="新しいパスワード"/>
                        </div>
                        <div className={styles.accountRoleChangeItemName}>
                            新しいパスワード（確認用） <input type="password" ref="newPasswordConfirmation" placeholder="新しいパスワード"/>
                        </div>
                    </div>
                </div>
                <div className={styles.accountRoleChangeButton}>
                 <div className={styles.accountRoleChangeButtonBox}><div onClick={this.accountPasswordChange.bind(this)} width="200"><img src="../icon/mono_icons/exchange32.png" className={styles.icon}/>パスワードを変更する <span ref="messageChangePassword"></span></div></div>
                </div>
               </div>

               <div className={styles.accountRoleChange}>
                <div>
                    <div className={styles.accountRoleChangeTitle}> 退会処理 </div>
                    <div className={styles.accountRoleChangeButton}>
                        <div className={styles.accountRoleChangeButtonBox}><div onClick={this.removeAccount.bind(this)} width="200"><img src="../icon/mono_icons/exchange32.png" className={styles.icon}/>退会する <span ref="messageRemoveAccount"></span></div></div>
                    </div>
                </div>
               </div>
               {(auth.getRole() == 'administrator' ? (
                                   <div className={styles.accountRoleChange}>
                                       <div>
                                                       <div className={styles.accountRoleChangeTitle}> 権限変更 </div>
                                                       <div className={styles.accountRoleChangeItemBox}>
                                                           <div className={styles.accountRoleChangeItemName}>
                                                               同じ会社コードを持つユーザーの権限を変更できます。E-mailアドレスを入力し、”一般”か”管理者”の権限どちらかをお選びください。
                                                           </div>
                                                           <input type="text" ref="roleEmail" placeholder="E-mailアドレス"/>
                                                           <input type="radio" name="newRole" value="none"/> 一般
                                                           <input type="radio" name="newRole" value="administrator"/> 管理者
                                                       </div>
                                                   </div>
                                                   <div className={styles.accountRoleChangeButton}>
                                                    <div className={styles.accountRoleChangeButtonBox}><div onClick={this.accountRoleChange.bind(this)} width="200"><img src="../icon/mono_icons/exchange32.png" className={styles.icon}/>変更 <span ref="message"></span></div></div>
                                                   </div>
                                   </div>
                                   ):(
                                       <div></div>
                                   )
               )}
               <Loading ref="loading"/>
           </div>
           )
    }
}