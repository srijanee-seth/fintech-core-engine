import streamlit as st
import requests
import pandas as pd

# --- 1. CONFIGURATION ---
st.set_page_config(page_title="Fintech Observability", layout="wide")
API_BASE_URL = "http://localhost:8080/api"
AUTH_CREDENTIALS = ("srijanee", "fintech2026")

# --- 2. HEADER ---
st.title("🤖 Fintech System Automation & Observability Dashboard")
st.markdown("---")

st.sidebar.header("📟 Core System Health")
st.sidebar.success("Gateway: Connected to Port 8080")
st.sidebar.info("Background Automation: Cron Active (10s)")

# --- 3. NETWORK ENGINE ---
def fetch_history(wallet_id):
    try:
        res = requests.get(f"{API_BASE_URL}/wallets/{wallet_id}/history", auth=AUTH_CREDENTIALS)
        return res.json() if res.status_code == 200 else None
    except:
        return None

def execute_deposit(wallet_id, amount):
    res = requests.put(
        f"{API_BASE_URL}/wallets/{wallet_id}/deposit", 
        params={"amount": amount}, 
        auth=AUTH_CREDENTIALS
    )
    return res

# --- 4. DATA PROCESSING & METRICS ---
wallet_target = 1
data = fetch_history(wallet_target)

if data:
    df = pd.DataFrame(data)
    
    # Calculate live balance securely from the ledger
    deposits = df[df['type'] == 'DEPOSIT']['amount'].sum()
    transfers_out = df[df['type'] == 'TRANSFER_OUT']['amount'].sum()
    live_balance = deposits - transfers_out
    
    # Render High-End Metric Cards
    st.subheader("Live System Metrics (Wallet 1)")
    col1, col2, col3 = st.columns(3)
    col1.metric("Total Value Locked (TVL)", f"₹ {live_balance:,.2f}")
    col2.metric("Ledger Volume", f"{len(df)} Transactions")
    col3.metric("System Status", "SECURE", delta="Encrypted")
    st.markdown("---")

    # --- 5. THE CONTROL ROOM (UI LAYOUT) ---
    left_column, right_column = st.columns([2, 1])

    with left_column:
        st.subheader("📊 System Ledger: Transaction Stream")
        display_df = df[["transactionId", "type", "amount", "timestamp", "walletId"]]
        st.dataframe(display_df, use_container_width=True, hide_index=True)

    with right_column:
        st.subheader("⚡ Command Node")
        st.info("Manual Override Controls")
        
        # The Interactive Deposit Form
        with st.form("deposit_form"):
            st.write("Inject Funds (Bypass GUI)")
            inject_amount = st.number_input("Amount (₹)", min_value=1.0, step=100.0)
            submit = st.form_submit_button("Execute Authorization")
            
            if submit:
                response = execute_deposit(wallet_target, inject_amount)
                if response.status_code == 200:
                    st.success(f"Successfully injected ₹{inject_amount}!")
                    st.rerun() # Instantly refreshes the dashboard with new data
                else:
                    # Catches the firewall error we built earlier!
                    error_msg = response.json().get('validation_error', 'Transaction Blocked')
                    st.error(error_msg)
else:
    st.warning("Awaiting connection to Spring Boot Core Engine...")