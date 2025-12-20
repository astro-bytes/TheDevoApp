# Getting Started

## Apple Developer Program
You must have an apple developer account enorder to build the app to the simulator.

> **Warning: This may need to be modified. It's been a minute since I've worked with anyone on a project and set it up other than just myself**
> Porter

1. Sign up for an account at [Apple](https://developer.apple.com/programs/enroll/) a basic account should work without having to pay annually.
2. Get access to The Devo App by being added by Porter McGary.
   - Send your email address used with your apple account
3. Login to your apple developer account in xcode

## Xcode 
We need to be using the same version of xcode other wise we end up running into build errors from one machine to another.

|Current Version|26.1.1|
|-|-|

### Install Xcodes: Xcode Management App (NOT to be confused with Xcode)
1. Run the following in the terminal
   ```bash
   brew install --cask xcodes
   ```
2. Open Xcodes
3. Login via settings using your Apple Account
   > **NOTE: This needs to be the same account you have setup as an apple developer**
4. Open the Advanced Settings in Xcodes and select "Always rename to Xcode.app" for the Active/Select option
5. Select and install the current version of Xcode we are using for our project
   > **NOTE: Be sure to set this version as the active version**

## Running the App 
For now we will focus on using just simulators. Physical devices make things complicated when working with more than one person on a project.

1. Open `clients/ios/TheDevoApp.xcodeproj` with Xcode
2. Select a iPhone Simulator
3. Press Play Button