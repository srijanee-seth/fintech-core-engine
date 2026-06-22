import streamlit as st
import requests
import pandas as pd

# ==========================================
# 🛑 CRITICAL STEP: Paste your Render URL here!
# Example: "https://fintech-core-api-xyz.onrender.com/api/wallets"
# ==========================================
API_BASE_URL = "https://fintech-core-engine.onrender.com/api/wallet" 

st.set_page_config(page_title="Fintech Core Dashboard", layout="wide")
st.title("🏦 Enterprise Fintech Engine")

# --- SIDEBAR: COMMAND NODE ---
st.sidebar.header("⚙️ Command Node")

st.sidebar.subheader("1. Create New Wallet")
new_wallet_balance = st.sidebar.number_input("Initial Balance", min_value=0.0, value=100.0, step=10.0)

if st.sidebar.button("Create Wallet"):
    # Added a visual spinner so we know if Render is just waking up!
    with st.spinner("Connecting to Cloud Server... (This may take 50s if the server is asleep)"):
        try:
            payload = {"balance": new_wallet_balance}
            response = requests.post(API_BASE_URL, json=payload)
            
            if response.status_code in [200, 201]:
                st.sidebar.success(f"Success! Wallet Created.")
                st.rerun() # This instantly refreshes the main dashboard table!
            else:
                # If it fails, print the EXACT error the Java server sent back
                st.sidebar.error(f"Failed! Status: {response.status_code}. Reason: {response.text}")
        except Exception as e:
            st.sidebar.error(f"Connection Error: {str(e)}")

# --- MAIN DASHBOARD AREA ---
st.write("### Live System Overview")

try:
    # Fetch all wallets from the Java API
    response = requests.get(API_BASE_URL)
    
    if response.status_code == 200:
        wallets = response.json()
        
        if not wallets:
            st.info("The vault is currently empty. Use the Command Node on the left to create a wallet!")
        else:
            # Calculate Total Value Locked (TVL)
            tvl = sum(w.get("balance", 0.0) for w in wallets)
            
            # Display metrics
            col1, col2 = st.columns(2)
            col1.metric("Total Value Locked (TVL)", f"${tvl:,.2f}")
            col2.metric("Active Wallets", len(wallets))
            
            # Display Data Table
            st.write("### Active Ledgers")
            df = pd.DataFrame(wallets)
            
            # Format the dataframe nicely
            if "balance" in df.columns:
                df["balance"] = df["balance"].apply(lambda x: f"${x:,.2f}")
            
            st.dataframe(df, use_container_width=True)
            
    else:
        st.error(f"Could not fetch data. Server returned status code: {response.status_code}")
except Exception as e:
    st.error("🚨 Could not connect to the Backend Engine. Did you paste your Render URL at the top of the code?")