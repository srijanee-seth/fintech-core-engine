import streamlit as st
import requests
import pandas as pd

# ==========================================
# 🛑 CRITICAL STEP: Keep your real Render URL here!
# ==========================================
API_BASE_URL = "https://fintech-core-engine.onrender.com/api/wallets" 

st.set_page_config(page_title="Fintech Core Dashboard", layout="wide")
st.title("🏦 Enterprise Fintech Engine")

# --- SIDEBAR: COMMAND NODE ---
st.sidebar.header("⚙️ Command Node")

# 1. CREATE WALLET
st.sidebar.subheader("1. Create New Wallet")
new_wallet_balance = st.sidebar.number_input("Initial Balance", min_value=0.0, value=100.0, step=10.0, key="create_bal")

if st.sidebar.button("Create Wallet"):
    with st.spinner("Communicating with Cloud Engine..."):
        try:
            payload = {"balance": new_wallet_balance}
            response = requests.post(API_BASE_URL, json=payload)
            if response.status_code in [200, 201]:
                st.sidebar.success("Success! Wallet Created.")
                st.rerun()
            else:
                st.sidebar.error(f"Failed! Reason: {response.text}")
        except Exception as e:
            st.sidebar.error(f"Connection Error: {str(e)}")

st.sidebar.divider()

# 2. DEPOSIT FUNDS
st.sidebar.subheader("2. Deposit Funds")
deposit_id = st.sidebar.number_input("Wallet ID", min_value=1, value=1, step=1, key="dep_id")
deposit_amount = st.sidebar.number_input("Deposit Amount", min_value=1.0, value=50.0, step=5.0, key="dep_amt")

if st.sidebar.button("Deposit"):
    with st.spinner("Processing Deposit..."):
        try:
            # Matches Java: @PutMapping("/{id}/deposit") with @RequestParam amount
            res = requests.put(f"{API_BASE_URL}/{deposit_id}/deposit", params={"amount": deposit_amount})
            if res.status_code == 200:
                st.sidebar.success(f"Deposited ${deposit_amount} into Wallet {deposit_id}!")
                st.rerun()
            else:
                st.sidebar.error(f"Deposit Failed: Wallet might not exist.")
        except Exception as e:
            st.sidebar.error("Connection Error.")

st.sidebar.divider()

# 3. TRANSFER FUNDS
st.sidebar.subheader("3. Transfer Money")
sender_id = st.sidebar.number_input("From Wallet ID", min_value=1, value=1, step=1, key="tx_sender")
receiver_id = st.sidebar.number_input("To Wallet ID", min_value=1, value=2, step=1, key="tx_receiver")
transfer_amount = st.sidebar.number_input("Transfer Amount", min_value=1.0, value=25.0, step=5.0, key="tx_amt")

if st.sidebar.button("Execute Transfer"):
    with st.spinner("Verifying ledger and transferring..."):
        try:
            # Matches Java: @PostMapping("/transfer") with @RequestParam senderId, receiverId, amount
            params = {"senderId": sender_id, "receiverId": receiver_id, "amount": transfer_amount}
            res = requests.post(f"{API_BASE_URL}/transfer", params=params)
            
            if res.status_code == 200:
                st.sidebar.success("Transfer Executed Successfully!")
                st.rerun()
            else:
                st.sidebar.error("Transfer Failed: Check balances and IDs.")
        except Exception as e:
            st.sidebar.error("Connection Error.")

# --- MAIN DASHBOARD AREA ---
st.write("### Live Cloud Database Overview")

try:
    response = requests.get(API_BASE_URL)
    
    if response.status_code == 200:
        wallets = response.json()
        
        if not wallets:
            st.info("The cloud database is currently empty. Use the Command Node on the left to create a wallet!")
        else:
            tvl = sum(w.get("balance", 0.0) for w in wallets)
            col1, col2 = st.columns(2)
            col1.metric("Total Value Locked (TVL)", f"${tvl:,.2f}")
            col2.metric("Active Wallets", len(wallets))
            
            st.write("### Active Ledgers")
            df = pd.DataFrame(wallets)
            
            # Format the dataframe nicely
            if "balance" in df.columns:
                df["balance"] = df["balance"].apply(lambda x: f"${x:,.2f}")
            
            st.dataframe(df, use_container_width=True)
            
    else:
        st.error(f"Could not fetch data. Server returned status code: {response.status_code}")
except Exception as e:
    st.error("🚨 Could not connect to the Backend Engine. Is your Render server awake?")