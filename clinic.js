let clinicState={clinics:[],members:[],clients:[],records:[],selectedClinic:"",inviteToken:"",message:"",role:""};
function cesc(v){return esc(String(v==null?"":v))}
async function loadClinicTools(){
 if(!cloudReady||!supabaseClient)return;
 const p=$("clinic-panel");if(!p)return;
 p.innerHTML='<p class="muted">Loading clinic workspace…</p>';
 try{
  let q=await supabaseClient.from("clinics").select("id,name,created_by").order("created_at",{ascending:false});if(q.error)throw q.error;
  clinicState.clinics=q.data||[];
  if(!clinicState.clinics.some(c=>c.id===clinicState.selectedClinic))clinicState.selectedClinic=clinicState.clinics[0]?.id||"";
  clinicState.members=[];clinicState.clients=[];clinicState.records=[];clinicState.role="";
  if(clinicState.selectedClinic){
   q=await supabaseClient.from("clinic_memberships").select("clinic_id,user_id,role,status").eq("clinic_id",clinicState.selectedClinic);if(q.error)throw q.error;
   clinicState.members=q.data||[];clinicState.role=clinicState.members.find(m=>m.user_id===cloudUser.id)?.role||"";
   q=await supabaseClient.from("clinic_clients").select("id,clinic_id,display_name,created_at").eq("clinic_id",clinicState.selectedClinic).order("created_at",{ascending:false});if(q.error)throw q.error;clinicState.clients=q.data||[];
   q=await supabaseClient.from("clinical_records").select("id,client_id,record_type,payload,created_at").eq("clinic_id",clinicState.selectedClinic).order("created_at",{ascending:false}).limit(30);if(q.error)throw q.error;clinicState.records=q.data||[];
  }
  renderClinicTools();
 }catch(e){p.innerHTML='<h2>Clinic workspace</h2><p class="danger-text">Could not load: '+cesc(e.message)+'</p><button class="btn ghost" data-action="clinic-refresh">Retry</button>'}
}
function renderClinicTools(){
 const p=$("clinic-panel");if(!p)return;
 const c=clinicState.clinics.find(x=>x.id===clinicState.selectedClinic),owner=!!c&&c.created_by===cloudUser?.id;
 const options=clinicState.clinics.map(x=>'<option value="'+cesc(x.id)+'" '+(x.id===clinicState.selectedClinic?'selected':'')+'>'+cesc(x.name)+'</option>').join("");
 const clients=clinicState.clients.map(x=>'<option value="'+cesc(x.id)+'">'+cesc(x.display_name)+'</option>').join("");
 const parents=clinicState.members.filter(x=>x.role==="parent"&&x.status==="active").map(x=>'<option value="'+cesc(x.user_id)+'">'+cesc(x.user_id.slice(0,8))+'… parent</option>').join("");
 p.innerHTML='<h2>Clinic workspace</h2><p class="muted">Clinic records are separate from your personal workspace and protected by database policies.</p><div class="clinic-layout"><div><form id="clinic-create-form"><label class="field">Create clinic<input name="clinic_name" required minlength="2" maxlength="120" placeholder="Clinic name"></label><button class="btn">Create clinic</button></form><form id="clinic-accept-form"><label class="field">Invitation token<input name="invite_token" required placeholder="Paste token"></label><button class="btn secondary">Join clinic</button></form></div><div><label class="field">Your clinics<select id="clinic-select" '+(options?'':'disabled')+'>'+options+'</select></label>'+(c?'<div class="clinic-item"><strong>'+cesc(c.name)+'</strong><p class="muted">Role: '+cesc(clinicState.role||"member")+'</p></div>':'<p class="muted">Create or join a clinic.</p>')+'</div></div>'+
(c&&owner?'<div class="clinic-item"><h3>Invite member</h3><p class="muted">Token expires in 7 days and only works for the invited email.</p><form id="clinic-invite-form"><label class="field">Email<input name="invite_email" type="email" required></label><label class="field">Role<select name="invite_role"><option value="student_clinician">Student clinician</option><option value="parent">Parent</option><option value="therapist">Therapist</option></select></label><button class="btn">Generate invitation token</button></form>'+(clinicState.inviteToken?'<div class="token-box">'+cesc(clinicState.inviteToken)+'</div><button class="btn ghost" data-action="clinic-copy-token">Copy token</button>':'')+'</div>':'')+
(c&&["therapist","student_clinician"].includes(clinicState.role)?'<div class="clinic-item"><h3>Clinic clients</h3><form id="clinic-client-form"><label class="field">Client display name<input name="client_name" required maxlength="120"></label><button class="btn">Add client</button></form>'+(clinicState.clients.map(x=>'<p class="clinic-record">'+cesc(x.display_name)+'</p>').join("")||'<p class="muted">No clinic clients yet.</p>')+'</div>':'')+
(c&&owner&&parents&&clinicState.clients.length?'<div class="clinic-item"><h3>Share with parent</h3><form id="clinic-share-form"><label class="field">Client<select name="client_id">'+clients+'</select></label><label class="field">Parent<select name="parent_id">'+parents+'</select></label><button class="btn secondary">Grant read access</button></form></div>':'')+
(c&&["therapist","student_clinician"].includes(clinicState.role)&&clinicState.clients.length?'<div class="clinic-item"><h3>Clinical records</h3><form id="clinic-record-form"><label class="field">Client<select name="record_client_id">'+clients+'</select></label><label class="field">Type<select name="record_type"><option value="goal">Goal</option><option value="activity">Activity / technique</option><option value="session_response">Session response</option><option value="therapy_diary">Therapy diary</option></select></label><label class="field">Factual note<textarea name="record_note" required maxlength="4000"></textarea></label><button class="btn">Save record</button></form>'+(clinicState.records.map(x=>'<div class="clinic-record"><strong>'+cesc(x.record_type.replaceAll("_"," "))+'</strong><p>'+cesc(x.payload?.note||"")+'</p><span class="muted">'+cesc(new Date(x.created_at).toLocaleString())+'</span></div>').join("")||'<p class="muted">No records yet.</p>')+'</div>':'')+
'<p id="clinic-message" class="auth-status" role="status">'+cesc(clinicState.message)+'</p><p class="muted">Use fictional data while testing. Parents only see clients explicitly shared with them. Personal data is not copied into clinics automatically.</p>';
}
document.addEventListener("submit",async e=>{
 const f=e.target;if(!f.id||!f.id.startsWith("clinic-")||!cloudReady)return;e.preventDefault();const v=new FormData(f);
 try{
  let q;
  if(f.id==="clinic-create-form"){q=await supabaseClient.rpc("create_speech_rehab_clinic",{p_name:String(v.get("clinic_name")||"").trim()});if(q.error)throw q.error;clinicState.selectedClinic=q.data;clinicState.inviteToken="";clinicState.message="Clinic created.";await loadClinicTools()}
  else if(f.id==="clinic-invite-form"){q=await supabaseClient.rpc("create_speech_rehab_invitation",{p_clinic_id:clinicState.selectedClinic,p_email:String(v.get("invite_email")||"").trim(),p_role:v.get("invite_role")});if(q.error)throw q.error;clinicState.inviteToken=q.data;clinicState.message="Share this token securely with the invited email.";renderClinicTools()}
  else if(f.id==="clinic-accept-form"){q=await supabaseClient.rpc("accept_speech_rehab_invitation",{p_token:String(v.get("invite_token")||"").trim()});if(q.error)throw q.error;clinicState.selectedClinic=q.data;clinicState.inviteToken="";clinicState.message="Invitation accepted.";await loadClinicTools()}
  else if(f.id==="clinic-client-form"){q=await supabaseClient.from("clinic_clients").insert({clinic_id:clinicState.selectedClinic,display_name:String(v.get("client_name")||"").trim(),created_by:cloudUser.id,data:{}});if(q.error)throw q.error;clinicState.message="Client created.";await loadClinicTools()}
  else if(f.id==="clinic-share-form"){q=await supabaseClient.from("client_access").upsert({client_id:v.get("client_id"),user_id:v.get("parent_id"),access_level:"read",granted_by:cloudUser.id},{onConflict:"client_id,user_id"});if(q.error)throw q.error;clinicState.message="Parent read access granted.";await loadClinicTools()}
  else if(f.id==="clinic-record-form"){q=await supabaseClient.from("clinical_records").insert({clinic_id:clinicState.selectedClinic,client_id:v.get("record_client_id"),record_type:v.get("record_type"),payload:{note:String(v.get("record_note")||"").trim()},created_by:cloudUser.id});if(q.error)throw q.error;clinicState.message="Record saved.";await loadClinicTools()}
 }catch(err){clinicState.message=err.message||"Action failed.";renderClinicTools()}
});
document.addEventListener("change",e=>{if(e.target.id==="clinic-select"){clinicState.selectedClinic=e.target.value;clinicState.inviteToken="";loadClinicTools()}});
document.addEventListener("click",e=>{if(e.target.closest('[data-action="clinic-refresh"]'))loadClinicTools();if(e.target.closest('[data-action="clinic-copy-token"]')&&clinicState.inviteToken){if(navigator.clipboard?.writeText)navigator.clipboard.writeText(clinicState.inviteToken).then(()=>{clinicState.message="Token copied.";renderClinicTools()}).catch(()=>{clinicState.message="Copy failed; copy the token manually.";renderClinicTools()})}});
