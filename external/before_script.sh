sh

# fix gradle license errors
mkdir -p "/usr/local/android-sdk/licenses"
echo -e "\n8933bad161af4178b1185d1a37fbf41ea5269c55" > "/usr/local/android-sdk/licenses/android-sdk-license"
echo -e "\n84831b9409646a918e30573bab4c9c91346d8abd" > "/usr/local/android-sdk/licenses/android-sdk-preview-license"
