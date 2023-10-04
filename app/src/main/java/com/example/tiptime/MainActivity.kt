/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    //override  overriding the onCreate for a parent class (e.g., AppCompatActivity
//comment
//(savedInstanceState: Bundle?. is object that can
// be used to save and restore the activity's state.
    override fun onCreate(savedInstanceState: Bundle?) {

        //super refers to the superclass of ur current class
        // (the activity or fragment)
       // savedInstanceState is a Bundle object that can be used to save and restore the state
      //  of your activity or fragment
        super.onCreate(savedInstanceState)

//setContentView is a method used in Android activities to set the user interface
// layout for the activity.This is the entry point
        setContent {

            //TipTimeTheme is a custom theme, and it wraps the UI composition within its scope
            TipTimeTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    //Surface is a Compose component that represents a
                    // rectangular area on the screen. It's often used as a
                    // container for other UI elements. In this case, it uses
                    // the background color
                    // defined in the MaterialTheme.colorScheme.background
                    // property.

                    modifier = Modifier.fillMaxSize(),
                    //This modifier is applied to the Surface and instructs it to take up the
                    // maximum available space within its parent.
                    color = MaterialTheme.colorScheme.background
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

//helps to draw ui component for e.g buttons, text , field etc in columns
@Composable
fun TipTimeLayout() {
    var amountInput by remember { mutableStateOf("") } //means initial value null
    var tipInput by remember { mutableStateOf("") }
    var roundUp by remember { mutableStateOf(false) }

    //remember keeps value entered by user saved when mobile is rotated and not reset the value
    //mutableStateOf allows user to type any change in value
    //var These are mutable (value can be changed). values can be entered by typing via keyboard. used to declare variable
    //val  Use val for a variable whose value never changes. You can't reassign a value to a variable that was declared using val.

    val amount = amountInput.toDoubleOrNull() ?: 0.0 //converts the amount input value entered by user into double and inserts in the val variable amountinput
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0
    val tip = calculateTip(amount, tipPercent, roundUp) //calls function for calc tip

//?: 0.0 syntax for declaring value of double e.g no. of decimal places

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .padding(40.dp)
                //This is a modifier applied to a Composable that enables
            // vertical
            // scrolling for the content within that Composable.
            // It allows the user to scroll vertically if the content
            // exceeds the available screen space
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(alignment = Alignment.Start)
        )
        EditNumberField(
            label = R.string.bill_amount,
            leadingIcon = R.drawable.money,
            //This part of the code starts with the default keyboard options and then
            // creates a copy with modifications.
            keyboardOptions = KeyboardOptions.Default.copy(
//This line specifies that the keyboard should be of the "Number" type
                keyboardType = KeyboardType.Number,
                //This line sets the IME (Input Method Editor) action to "Next.
                imeAction = ImeAction.Next
            ),
            value = amountInput,
            onValueChanged = { amountInput = it },
            //Modifier is a class in Jetpack Compose used to
            // Composable elements, such as padding, size, alignment
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
        )
        EditNumberField(
            label = R.string.how_was_the_service,
            leadingIcon = R.drawable.percent,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            value = tipInput,
            onValueChanged = { tipInput = it },
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
        )
        RoundTheTipRow(
            roundUp = roundUp,
            //onValueChanged callback is invoked when the value of the input field changes.
            onRoundUpChanged = { roundUp = it },
// It specifies what action should be taken when the value of
// the input field changes. In this case:
//
//it refers to the new value of the input field.
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Text(
            text = stringResource(R.string.tip_amount, tip),
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(150.dp))
    }
}

//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNumberField(
    @StringRes label: Int,
    @DrawableRes leadingIcon: Int,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChanged: (String) -> Unit,
    //onValueChanged. It specifies that onValueChanged is a function that takes a single parameter
    // of type String and does not return anything (Unit).
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        singleLine = true,
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), null) },
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun RoundTheTipRow(
    roundUp: Boolean,
    onRoundUpChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(48.dp),
    ) {
        Text(text = stringResource(R.string.round_up_tip))
        Switch(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End), //move sliding bar to available space
            checked = roundUp,
            onCheckedChange = onRoundUpChanged
        )
    }
}

/**
 * Calculates the tip based on the user input and format the tip amount
 * according to the local currency.
 * Example would be "$10.00".
 */
private fun calculateTip(amount: Double, tipPercent: Double = 15.0, roundUp: Boolean): String {
    var tip = tipPercent / 100 * amount
    if (roundUp) {
        // performing a rounding operation in Kotlin based on the
        // value of the roundUp variable
        tip = kotlin.math.ceil(tip)
    }
    // It takes the numerical value tip and returns a formatted string representation of
    // that value in the currency format.
    return NumberFormat.getCurrencyInstance().format(tip)
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}